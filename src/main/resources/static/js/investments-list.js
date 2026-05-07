// ── Stage visibility ──────────────────────────────────────
function updateNewStageVisibility() {
    const type      = document.getElementById('newTypeSelect').value;
    const wrapper   = document.getElementById('newStageWrapper');
    const select    = document.getElementById('newStageSelect');
    const isPrivate = type === 'PRIVATE';
    wrapper.classList.toggle('d-none', !isPrivate);
    select.disabled = !isPrivate;
    if (!isPrivate) {
        select.value = '';
        select.classList.remove('is-invalid', 'is-valid');
        const fb = select.parentElement.querySelector('.invalid-feedback');
        if (fb) fb.textContent = '';
    }
}
document.getElementById('newTypeSelect').addEventListener('change', updateNewStageVisibility);

// ── Validation helper ─────────────────────────────────────
function setValidity(el, isValid, msg) {
    el.classList.toggle('is-invalid', !isValid);
    el.classList.toggle('is-valid', isValid);
    const fb = el.parentElement.querySelector('.invalid-feedback');
    if (fb) fb.textContent = msg || '';
}

// ── Add-investment form validation ───────────────────────
function validateAddForm() {
    let ok = true;

    const name = document.getElementById('newName');
    const nv   = name.value.trim();
    if (!nv) {
        setValidity(name, false, 'Investment name is required.');
        ok = false;
    } else if (nv.length < 2) {
        setValidity(name, false, 'Name must be at least 2 characters.');
        ok = false;
    } else if (nv.length > 100) {
        setValidity(name, false, 'Name cannot exceed 100 characters.');
        ok = false;
    } else {
        setValidity(name, true, '');
    }

    const type = document.getElementById('newTypeSelect');
    if (!type.value) {
        setValidity(type, false, 'Please select an investment type.');
        ok = false;
    } else {
        setValidity(type, true, '');
    }

    const sector = document.getElementById('newSector');
    if (!sector.value) {
        setValidity(sector, false, 'Please select a sector.');
        ok = false;
    } else {
        setValidity(sector, true, '');
    }

    const risk = document.getElementById('newRiskLevel');
    if (!risk.value) {
        setValidity(risk, false, 'Please select a risk level.');
        ok = false;
    } else {
        setValidity(risk, true, '');
    }

    const price = document.getElementById('newPricePerUnit');
    const pv    = parseFloat(price.value);
    if (!price.value || isNaN(pv) || pv <= 0) {
        setValidity(price, false, 'Price must be greater than $0.00.');
        ok = false;
    } else {
        setValidity(price, true, '');
    }

    const qty = document.getElementById('newQuantity');
    const qv  = parseFloat(qty.value);
    if (!qty.value || isNaN(qv) || qv < 1 || !Number.isInteger(qv)) {
        setValidity(qty, false, 'Quantity must be a whole number of at least 1.');
        ok = false;
    } else {
        setValidity(qty, true, '');
    }

    if (type.value === 'PRIVATE') {
        const stage = document.getElementById('newStageSelect');
        if (!stage.value) {
            setValidity(stage, false, 'Please select a stage for this private investment.');
            ok = false;
        } else {
            setValidity(stage, true, '');
        }
    }

    if (!checkDryPowder()) ok = false;

    return ok;
}

let addSubmitted = false;
document.getElementById('addInvestmentForm').addEventListener('submit', function (e) {
    addSubmitted = true;
    if (!validateAddForm()) e.preventDefault();
});

['newName', 'newPricePerUnit', 'newQuantity'].forEach(function (id) {
    document.getElementById(id).addEventListener('input', function () {
        if (addSubmitted) validateAddForm();
    });
});
['newTypeSelect', 'newSector', 'newRiskLevel', 'newStageSelect'].forEach(function (id) {
    document.getElementById(id).addEventListener('change', function () {
        if (addSubmitted) validateAddForm();
    });
});

// ── Dry powder check + estimated preview ─────────────────
const dryPowder = parseFloat(document.getElementById('addInvestmentForm').dataset.dryPowder) || 0;

function checkDryPowder() {
    const p         = parseFloat(document.getElementById('newPricePerUnit').value) || 0;
    const q         = parseInt(document.getElementById('newQuantity').value)        || 0;
    const estimated = p * q;
    const preview   = document.getElementById('newInvestedPreview');
    const errorDiv  = document.getElementById('dryPowderError');

    if (estimated > 0 && estimated > dryPowder) {
        preview.classList.add('text-danger');
        preview.classList.remove('text-primary');
        errorDiv.style.display = 'block';
        errorDiv.textContent =
            'Insufficient funds — estimated $' +
            estimated.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) +
            ' exceeds available dry powder $' +
            dryPowder.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + '.';
        return false;
    }
    preview.classList.remove('text-danger');
    preview.classList.add('text-primary');
    errorDiv.style.display = 'none';
    errorDiv.textContent   = '';
    return true;
}

function updateNewFormPreview() {
    const p = parseFloat(document.getElementById('newPricePerUnit').value) || 0;
    const q = parseInt(document.getElementById('newQuantity').value)        || 0;
    document.getElementById('newInvestedPreview').textContent =
        '$' + (p * q).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    checkDryPowder();
}
document.getElementById('newPricePerUnit').addEventListener('input', updateNewFormPreview);
document.getElementById('newQuantity').addEventListener('input', updateNewFormPreview);

// ── Exit-modal validation ─────────────────────────────────
document.querySelectorAll('.exit-value-input').forEach(function (input) {
    const form = input.closest('form');
    let submitted = false;
    form.addEventListener('submit', function (e) {
        submitted = true;
        const v     = parseFloat(input.value);
        const valid = !!input.value && !isNaN(v) && v > 0;
        input.classList.toggle('is-invalid', !valid);
        input.classList.toggle('is-valid', valid);
        const fb = input.parentElement.querySelector('.invalid-feedback');
        if (fb) fb.textContent = valid ? '' : 'Exit value must be greater than $0.00.';
        if (!valid) e.preventDefault();
    });
    input.addEventListener('input', function () {
        if (!submitted) return;
        const v     = parseFloat(input.value);
        const valid = !!input.value && !isNaN(v) && v > 0;
        input.classList.toggle('is-invalid', !valid);
        input.classList.toggle('is-valid', valid);
        const fb = input.parentElement.querySelector('.invalid-feedback');
        if (fb) fb.textContent = valid ? '' : 'Exit value must be greater than $0.00.';
    });
});

// ── Client-side table sorting ─────────────────────────────
document.querySelectorAll('.sort-btn').forEach(function (btn) {
    btn.addEventListener('click', function () {
        const sortKey = this.dataset.sort;
        const tbody   = document.querySelector('table tbody');
        if (!tbody) return;

        const rows = Array.from(tbody.querySelectorAll('tr'));
        rows.sort(function (a, b) {
            if (sortKey === 'name') {
                return a.dataset.name.localeCompare(b.dataset.name);
            } else if (sortKey === 'amount_asc') {
                return parseFloat(a.dataset.amount) - parseFloat(b.dataset.amount);
            } else if (sortKey === 'amount_desc') {
                return parseFloat(b.dataset.amount) - parseFloat(a.dataset.amount);
            }
            return 0;
        });
        rows.forEach(function (row) { tbody.appendChild(row); });

        document.querySelectorAll('.sort-btn').forEach(function (b) {
            b.classList.remove('btn-primary');
            b.classList.add('btn-outline-secondary');
        });
        this.classList.add('btn-primary');
        this.classList.remove('btn-outline-secondary');
    });
});
