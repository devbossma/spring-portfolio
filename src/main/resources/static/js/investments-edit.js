// ── Stage visibility ──────────────────────────────────────
function updateEditStageVisibility() {
    const type      = document.getElementById('editTypeSelect').value;
    const wrapper   = document.getElementById('editStageWrapper');
    const select    = document.getElementById('editStageSelect');
    const isPrivate = type === 'PRIVATE';
    wrapper.classList.toggle('d-none', !isPrivate);
    select.disabled = !isPrivate;
}
document.getElementById('editTypeSelect').addEventListener('change', updateEditStageVisibility);
updateEditStageVisibility();

// ── Live calculations ─────────────────────────────────────
function updateCalculations() {
    const price = parseFloat(document.getElementById('pricePerUnit').value) || 0;
    const qty   = parseInt(document.getElementById('quantity').value)       || 0;
    const cur   = parseFloat(document.getElementById('currentValue').value) || 0;
    document.getElementById('investedAmount').textContent    = (price * qty).toFixed(2);
    document.getElementById('currentTotalValue').textContent = (cur * qty).toFixed(2);
}
['pricePerUnit', 'quantity', 'currentValue'].forEach(function (id) {
    document.getElementById(id).addEventListener('input', updateCalculations);
});
updateCalculations();

// ── Validation ────────────────────────────────────────────
function setValidity(el, isValid, msg) {
    el.classList.toggle('is-invalid', !isValid);
    el.classList.toggle('is-valid', isValid);
    const fb = el.parentElement.querySelector('.invalid-feedback');
    if (fb) fb.textContent = msg || '';
}

function validateEditForm() {
    let ok = true;

    const name = document.getElementById('editName');
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

    const price = document.getElementById('pricePerUnit');
    const pv    = parseFloat(price.value);
    if (!price.value || isNaN(pv) || pv <= 0) {
        setValidity(price, false, 'Price must be greater than $0.00.');
        ok = false;
    } else {
        setValidity(price, true, '');
    }

    const qty = document.getElementById('quantity');
    const qv  = parseFloat(qty.value);
    if (!qty.value || isNaN(qv) || qv < 1 || !Number.isInteger(qv)) {
        setValidity(qty, false, 'Quantity must be a whole number of at least 1.');
        ok = false;
    } else {
        setValidity(qty, true, '');
    }

    const cur = document.getElementById('currentValue');
    const cv  = parseFloat(cur.value);
    if (cur.value === '' || isNaN(cv) || cv < 0) {
        setValidity(cur, false, 'Current value cannot be negative.');
        ok = false;
    } else {
        setValidity(cur, true, '');
    }

    return ok;
}

let editSubmitted = false;
document.getElementById('editForm').addEventListener('submit', function (e) {
    editSubmitted = true;
    if (!validateEditForm()) e.preventDefault();
});

['editName', 'pricePerUnit', 'quantity', 'currentValue'].forEach(function (id) {
    document.getElementById(id).addEventListener('input', function () {
        if (editSubmitted) validateEditForm();
    });
});
