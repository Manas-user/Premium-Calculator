# Ansible Deployment Automation — Premium Calculator

This directory contains Ansible automation playbooks and configuration for deploying, configuring, and orchestrating the **Premium Calculator** containerized application.

---

## 1. Architecture Overview

Ansible operates as an agentless configuration management and orchestration tool.
- **Control Node**: The machine running Ansible (Linux or Windows WSL 2).
- **Managed Nodes**: Target servers running Docker (defined in `inventory.ini`).
- **Playbook**: `playbook.yml` ensures idempotent state management (pulling image, recreating container with optimal memory limits, and polling `/actuator/health`).

---

## 2. Windows 10/11 Setup (via WSL 2)

Ansible does not run natively inside Windows PowerShell because it relies on POSIX system calls. On Windows 10/11, run Ansible inside **WSL 2 (Ubuntu/Debian)**:

### Step 1: Open WSL Terminal
In Windows PowerShell, run:
```powershell
wsl
```

### Step 2: Install Ansible and Python Docker dependencies
Inside WSL:
```bash
sudo apt update
sudo apt install -y python3-pip python3-venv ansible docker.io
sudo usermod -aG docker $USER
```

### Step 3: Install Required Galaxy Collections
Navigate to the project's ansible folder:
```bash
cd /mnt/c/Users/manas/.gemini/antigravity/scratch/premium-calculator/ansible
ansible-galaxy collection install -r requirements.yml
```

---

## 3. Running the Playbook

### Syntax Verification
Check playbook syntax before execution:
```bash
ansible-playbook -i inventory.ini playbook.yml --syntax-check
```

### Dry Run (Check Mode)
Preview changes without applying:
```bash
ansible-playbook -i inventory.ini playbook.yml --check
```

### Deploy Locally / to WSL Docker
```bash
ansible-playbook -i inventory.ini playbook.yml
```

### Deploy to Remote Production / Staging Server
1. Update `inventory.ini` with target IP and SSH credentials:
   ```ini
   [app_servers]
   prod-server ansible_host=192.168.1.50 ansible_user=ubuntu ansible_ssh_private_key_file=~/.ssh/id_rsa
   ```
2. Execute:
   ```bash
   ansible-playbook -i inventory.ini playbook.yml
   ```

---

## 4. Playbook Features & Idempotency

- **Idempotent Container Management**: If the container is already running with the target image and configuration, Ansible makes no unnecessary changes.
- **Dynamic Health Check**: Polls `http://localhost:8080/actuator/health` up to 10 retries with 3-second delays to verify application readiness before reporting success.
- **Non-root Enforcement**: Pairs with the non-root execution inside Docker.
- **No Committed Secrets**: All secrets or credentials are externalized or injected via Ansible Vault or environment variables.
