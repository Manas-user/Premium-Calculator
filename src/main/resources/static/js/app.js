/**
 * Premium Calculator — Frontend Application Logic
 * Integrates with Spring Boot REST Endpoints:
 * - POST  /api/calculations
 * - GET   /api/transactions
 * - GET   /api/transactions/{id}
 * - PATCH /api/transactions/{id}/status
 * - GET   /api/transactions/{id}/receipt
 * - GET   /actuator/health
 */

document.addEventListener('DOMContentLoaded', () => {
    // --- State ---
    let transactionsState = [];
    let activeTransaction = null;

    // --- DOM Elements ---
    const healthDot = document.getElementById('healthDot');
    const healthText = document.getElementById('healthText');

    const formAlert = document.getElementById('formAlert');
    const tableAlert = document.getElementById('tableAlert');

    const calculatorForm = document.getElementById('calculatorForm');
    const customerNameInput = document.getElementById('customerName');
    const selectedPolicyType = document.getElementById('selectedPolicyType');
    const policyCardOptions = document.querySelectorAll('.policy-card-option');
    const coverageAmountInput = document.getElementById('coverageAmount');
    const policyTermSlider = document.getElementById('policyTermSlider');
    const policyTermDisplay = document.getElementById('policyTermDisplay');
    const shortcutBtns = document.querySelectorAll('.shortcut-btn');
    const calculateSubmitBtn = document.getElementById('calculateSubmitBtn');
    const btnSpinner = document.getElementById('btnSpinner');
    const btnText = document.getElementById('btnText');
    const resetFormBtn = document.getElementById('resetFormBtn');

    // Estimate Summary Elements
    const estimateSummaryPanel = document.getElementById('estimateSummaryPanel');
    const summaryStatusBadge = document.getElementById('summaryStatusBadge');
    const summaryTxId = document.getElementById('summaryTxId');
    const summaryRate = document.getElementById('summaryRate');
    const summaryAnnualPremium = document.getElementById('summaryAnnualPremium');
    const summaryTotalPremium = document.getElementById('summaryTotalPremium');
    const summaryDownloadReceiptBtn = document.getElementById('summaryDownloadReceiptBtn');
    const summaryApproveBtn = document.getElementById('summaryApproveBtn');
    const summaryRejectBtn = document.getElementById('summaryRejectBtn');

    // Table & Filter Elements
    const searchInput = document.getElementById('searchInput');
    const statusFilter = document.getElementById('statusFilter');
    const refreshBtn = document.getElementById('refreshBtn');
    const transactionsTbody = document.getElementById('transactionsTbody');
    const emptyState = document.getElementById('emptyState');

    // Modal Elements
    const transactionModal = document.getElementById('transactionModal');
    const modalCloseBtn = document.getElementById('modalCloseBtn');
    const modalDoneBtn = document.getElementById('modalDoneBtn');
    const modalTxId = document.getElementById('modalTxId');
    const modalStatus = document.getElementById('modalStatus');
    const modalCustomerName = document.getElementById('modalCustomerName');
    const modalPolicyType = document.getElementById('modalPolicyType');
    const modalCoverage = document.getElementById('modalCoverage');
    const modalTerm = document.getElementById('modalTerm');
    const modalRate = document.getElementById('modalRate');
    const modalAnnual = document.getElementById('modalAnnual');
    const modalTotal = document.getElementById('modalTotal');
    const modalCreatedAt = document.getElementById('modalCreatedAt');
    const modalDownloadReceiptBtn = document.getElementById('modalDownloadReceiptBtn');
    const modalApproveBtn = document.getElementById('modalApproveBtn');
    const modalRejectBtn = document.getElementById('modalRejectBtn');

    // --- Formatters ---
    const currencyFormatter = new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        maximumFractionDigits: 2
    });

    function formatCurrency(val) {
        if (val === null || val === undefined) return '₹0.00';
        return currencyFormatter.format(val);
    }

    function formatDate(isoStr) {
        if (!isoStr) return '-';
        try {
            const date = new Date(isoStr);
            return date.toLocaleString('en-IN', {
                year: 'numeric',
                month: 'short',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
        } catch {
            return isoStr;
        }
    }

    function showAlert(elem, msg, type = 'danger') {
        elem.className = `alert-box alert-${type}`;
        elem.innerHTML = msg;
        elem.style.display = 'block';
        setTimeout(() => {
            elem.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        }, 50);
    }

    function hideAlert(elem) {
        elem.style.display = 'none';
        elem.innerHTML = '';
    }

    // --- Health Check ---
    async function checkBackendHealth() {
        try {
            const res = await fetch('/actuator/health');
            if (res.ok) {
                const data = await res.json();
                if (data.status === 'UP') {
                    healthDot.className = 'health-dot online';
                    healthText.textContent = 'Backend: UP';
                    return;
                }
            }
            healthDot.className = 'health-dot offline';
            healthText.textContent = 'Backend: Degraded';
        } catch (err) {
            healthDot.className = 'health-dot offline';
            healthText.textContent = 'Backend: Offline';
        }
    }

    // --- UI Event Listeners ---

    // Policy selector cards
    policyCardOptions.forEach(card => {
        card.addEventListener('click', () => {
            policyCardOptions.forEach(c => {
                c.classList.remove('selected');
                c.setAttribute('aria-pressed', 'false');
            });
            card.classList.add('selected');
            card.setAttribute('aria-pressed', 'true');
            selectedPolicyType.value = card.getAttribute('data-policy');
        });
        card.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                card.click();
            }
        });
    });

    // Term slider
    policyTermSlider.addEventListener('input', (e) => {
        policyTermDisplay.textContent = `${e.target.value} yrs`;
    });

    // Coverage shortcut buttons
    shortcutBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            coverageAmountInput.value = btn.getAttribute('data-amt');
        });
    });

    // Reset Form
    resetFormBtn.addEventListener('click', () => {
        calculatorForm.reset();
        policyCardOptions[0].click(); // default to LIFE
        policyTermSlider.value = 5;
        policyTermDisplay.textContent = '5 yrs';
        coverageAmountInput.value = 100000;
        estimateSummaryPanel.style.display = 'none';
        activeTransaction = null;
        hideAlert(formAlert);
    });

    // Form Submit
    calculatorForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        hideAlert(formAlert);

        const customerName = customerNameInput.value.trim();
        const policyType = selectedPolicyType.value;
        const coverageAmount = parseFloat(coverageAmountInput.value);
        const policyTerm = parseInt(policyTermSlider.value, 10);

        // Frontend validation
        if (!customerName || customerName.length < 2 || customerName.length > 60) {
            showAlert(formAlert, 'Customer name is required and must be between 2 and 60 characters.');
            customerNameInput.focus();
            return;
        }

        if (isNaN(coverageAmount) || coverageAmount < 1000 || coverageAmount > 10000000) {
            showAlert(formAlert, 'Coverage amount must be between ₹1,000 and ₹10,000,000.');
            coverageAmountInput.focus();
            return;
        }

        if (isNaN(policyTerm) || policyTerm < 1 || policyTerm > 50) {
            showAlert(formAlert, 'Policy term must be between 1 and 50 years.');
            return;
        }

        const payload = {
            customerName,
            policyType,
            coverageAmount,
            policyTerm
        };

        // Loading state
        calculateSubmitBtn.disabled = true;
        btnSpinner.style.display = 'inline-block';
        btnText.textContent = 'Calculating...';

        try {
            const res = await fetch('/api/calculations', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await res.json();

            if (!res.ok) {
                const errMsg = data.details && data.details.length > 0
                    ? data.details.join('<br>')
                    : (data.message || 'Error occurred during calculation.');
                showAlert(formAlert, `<strong>Submission failed:</strong><br>${errMsg}`);
                return;
            }

            // Success: update estimate summary
            activeTransaction = data;
            renderEstimateSummary(data);
            showAlert(formAlert, `✓ Estimate calculated successfully! Transaction ID: <strong>${data.id}</strong>`, 'success');

            // Refresh transaction history table
            await loadTransactions();

        } catch (err) {
            showAlert(formAlert, 'Could not connect to the backend server. Verify the Spring Boot application is running.');
        } finally {
            calculateSubmitBtn.disabled = false;
            btnSpinner.style.display = 'none';
            btnText.textContent = 'Calculate & Submit Estimate';
        }
    });

    function renderEstimateSummary(tx) {
        summaryTxId.textContent = tx.id;
        summaryRate.textContent = `${tx.policyDisplayName} (${tx.ratePercentage})`;
        summaryAnnualPremium.textContent = formatCurrency(tx.annualPremium);
        summaryTotalPremium.textContent = formatCurrency(tx.totalPremium);

        summaryStatusBadge.textContent = tx.status;
        summaryStatusBadge.className = `badge badge-${tx.status}`;

        // Enable or disable actions based on status
        const isSubmitted = tx.status === 'SUBMITTED';
        summaryApproveBtn.disabled = !isSubmitted;
        summaryRejectBtn.disabled = !isSubmitted;

        estimateSummaryPanel.style.display = 'block';
    }

    // Estimate summary action listeners
    summaryDownloadReceiptBtn.addEventListener('click', () => {
        if (activeTransaction) downloadReceipt(activeTransaction.id);
    });

    summaryApproveBtn.addEventListener('click', () => {
        if (activeTransaction) updateTransactionStatus(activeTransaction.id, 'APPROVED', true);
    });

    summaryRejectBtn.addEventListener('click', () => {
        if (activeTransaction) updateTransactionStatus(activeTransaction.id, 'REJECTED', true);
    });

    // --- Transactions List & Filter ---
    async function loadTransactions() {
        try {
            const res = await fetch('/api/transactions');
            if (res.ok) {
                transactionsState = await res.json();
                renderTransactionsTable();
            } else {
                showAlert(tableAlert, 'Failed to retrieve transactions from backend.');
            }
        } catch (err) {
            showAlert(tableAlert, 'Unable to load transaction records. Check backend connection.');
        }
    }

    function renderTransactionsTable() {
        const query = searchInput.value.trim().toLowerCase();
        const status = statusFilter.value;

        const filtered = transactionsState.filter(tx => {
            const matchesQuery = !query ||
                tx.id.toLowerCase().includes(query) ||
                tx.customerName.toLowerCase().includes(query);
            const matchesStatus = status === 'ALL' || tx.status === status;
            return matchesQuery && matchesStatus;
        });

        transactionsTbody.innerHTML = '';

        if (filtered.length === 0) {
            emptyState.style.display = 'block';
            return;
        }

        emptyState.style.display = 'none';

        filtered.forEach(tx => {
            const tr = document.createElement('tr');
            const isSubmitted = tx.status === 'SUBMITTED';

            tr.innerHTML = `
                <td style="font-family: monospace; font-weight: 600;">
                    <a href="#" class="view-tx-link" data-id="${tx.id}" style="color: var(--primary); text-decoration: none;">
                        ${tx.id}
                    </a>
                </td>
                <td style="font-weight: 500;">${escapeHtml(tx.customerName)}</td>
                <td><span style="font-size: 0.82rem;">${tx.policyDisplayName}</span></td>
                <td style="font-weight: 600;">${formatCurrency(tx.totalPremium)}</td>
                <td><span class="badge badge-${tx.status}">${tx.status}</span></td>
                <td style="text-align: right;">
                    <div style="display: inline-flex; gap: 0.35rem;">
                        <button class="btn btn-outline btn-sm view-btn" data-id="${tx.id}" title="View Details">👁️</button>
                        <button class="btn btn-outline btn-sm receipt-btn" data-id="${tx.id}" title="Download Receipt">📄</button>
                        ${isSubmitted ? `
                            <button class="btn btn-success btn-sm approve-btn" data-id="${tx.id}" title="Approve">✓</button>
                            <button class="btn btn-danger btn-sm reject-btn" data-id="${tx.id}" title="Reject">✕</button>
                        ` : ''}
                    </div>
                </td>
            `;

            transactionsTbody.appendChild(tr);
        });

        attachTableActionListeners();
    }

    function attachTableActionListeners() {
        document.querySelectorAll('.view-tx-link, .view-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const id = btn.getAttribute('data-id');
                openTransactionModal(id);
            });
        });

        document.querySelectorAll('.receipt-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.getAttribute('data-id');
                downloadReceipt(id);
            });
        });

        document.querySelectorAll('.approve-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.getAttribute('data-id');
                updateTransactionStatus(id, 'APPROVED');
            });
        });

        document.querySelectorAll('.reject-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.getAttribute('data-id');
                updateTransactionStatus(id, 'REJECTED');
            });
        });
    }

    searchInput.addEventListener('input', renderTransactionsTable);
    statusFilter.addEventListener('change', renderTransactionsTable);
    refreshBtn.addEventListener('click', () => {
        hideAlert(tableAlert);
        loadTransactions();
    });

    // --- Status Update API call ---
    async function updateTransactionStatus(id, newStatus, updateSummary = false) {
        try {
            const res = await fetch(`/api/transactions/${encodeURIComponent(id)}/status`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: newStatus })
            });

            const data = await res.json();

            if (!res.ok) {
                const msg = data.message || `Failed to update status to ${newStatus}.`;
                showAlert(tableAlert, `<strong>Update Failed:</strong> ${msg}`);
                return;
            }

            showAlert(tableAlert, `✓ Transaction <strong>${id}</strong> marked as <strong>${newStatus}</strong>.`, 'success');

            // Update in active state
            if (activeTransaction && activeTransaction.id === id) {
                activeTransaction = data;
                renderEstimateSummary(data);
            }

            // Close modal if open on this transaction
            if (transactionModal.classList.contains('open') && modalTxId.textContent === id) {
                populateModal(data);
            }

            await loadTransactions();

        } catch (err) {
            showAlert(tableAlert, 'Failed to update transaction status. Network error.');
        }
    }

    // --- Receipt Download ---
    function downloadReceipt(id) {
        const url = `/api/transactions/${encodeURIComponent(id)}/receipt`;
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `receipt-${id}.txt`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    // --- Modal Handling ---
    async function openTransactionModal(id) {
        try {
            const res = await fetch(`/api/transactions/${encodeURIComponent(id)}`);
            if (!res.ok) {
                showAlert(tableAlert, `Could not load transaction details for ID: ${id}`);
                return;
            }
            const data = await res.json();
            populateModal(data);
            transactionModal.classList.add('open');
        } catch (err) {
            showAlert(tableAlert, `Error retrieving transaction ${id}.`);
        }
    }

    function populateModal(tx) {
        modalTxId.textContent = tx.id;
        modalStatus.innerHTML = `<span class="badge badge-${tx.status}">${tx.status}</span>`;
        modalCustomerName.textContent = tx.customerName;
        modalPolicyType.textContent = `${tx.policyDisplayName} (${tx.policyType})`;
        modalCoverage.textContent = formatCurrency(tx.coverageAmount);
        modalTerm.textContent = `${tx.policyTerm} years`;
        modalRate.textContent = tx.ratePercentage;
        modalAnnual.textContent = formatCurrency(tx.annualPremium);
        modalTotal.textContent = formatCurrency(tx.totalPremium);
        modalCreatedAt.textContent = formatDate(tx.createdAt);

        const isSubmitted = tx.status === 'SUBMITTED';
        modalApproveBtn.style.display = isSubmitted ? 'inline-flex' : 'none';
        modalRejectBtn.style.display = isSubmitted ? 'inline-flex' : 'none';

        modalApproveBtn.onclick = () => updateTransactionStatus(tx.id, 'APPROVED');
        modalRejectBtn.onclick = () => updateTransactionStatus(tx.id, 'REJECTED');
        modalDownloadReceiptBtn.onclick = () => downloadReceipt(tx.id);
    }

    function closeModal() {
        transactionModal.classList.remove('open');
    }

    modalCloseBtn.addEventListener('click', closeModal);
    modalDoneBtn.addEventListener('click', closeModal);
    transactionModal.addEventListener('click', (e) => {
        if (e.target === transactionModal) closeModal();
    });

    function escapeHtml(str) {
        if (!str) return '';
        return str
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    // --- Initial Load ---
    checkBackendHealth();
    loadTransactions();
    setInterval(checkBackendHealth, 30000); // Check health every 30s
});
