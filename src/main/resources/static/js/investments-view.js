// ── Validation helper ─────────────────────────────────────
function setValidity(el, isValid, msg) {
    el.classList.toggle('is-invalid', !isValid);
    el.classList.toggle('is-valid', isValid);
    const fb = el.parentElement.querySelector('.invalid-feedback');
    if (fb) fb.textContent = msg || '';
}

// ── Update current value form ─────────────────────────────
const updateForm = document.getElementById('updateValueForm');
if (updateForm) {
    const updateInput   = document.getElementById('updateCurrentValue');
    const updateWarning = document.getElementById('updateZeroWarning');
    let updateSubmitted = false;

    function checkUpdateValue(showErrors) {
        const raw = updateInput.value;
        const v   = parseFloat(raw);
        const fb  = updateInput.parentElement.querySelector('.invalid-feedback');

        if (raw === '' || isNaN(v) || v < 0) {
            if (showErrors) {
                updateInput.classList.add('is-invalid');
                updateInput.classList.remove('is-valid');
                if (fb) fb.textContent = 'Please enter a valid value ($0.00 or greater).';
            }
            updateInput.style.borderColor = '';
            updateInput.style.boxShadow   = '';
            updateWarning.style.display   = 'none';
            return false;
        } else if (v === 0) {
            updateInput.classList.remove('is-invalid', 'is-valid');
            updateInput.style.borderColor = '#ffc107';
            updateInput.style.boxShadow   = '0 0 0 0.25rem rgba(255,193,7,0.25)';
            updateWarning.style.display   = 'block';
            if (fb) fb.textContent = '';
            return true;
        } else {
            updateInput.classList.remove('is-invalid');
            updateInput.classList.add('is-valid');
            updateInput.style.borderColor = '';
            updateInput.style.boxShadow   = '';
            updateWarning.style.display   = 'none';
            if (fb) fb.textContent = '';
            return true;
        }
    }

    updateForm.addEventListener('submit', function (e) {
        updateSubmitted = true;
        if (!checkUpdateValue(true)) e.preventDefault();
    });

    updateInput.addEventListener('input', function () {
        checkUpdateValue(updateSubmitted);
    });
}

// ── Exit modal form ───────────────────────────────────────
const exitForm = document.getElementById('viewExitForm');
if (exitForm) {
    const exitInput = document.getElementById('viewExitValue');
    let exitSubmitted = false;

    exitForm.addEventListener('submit', function (e) {
        exitSubmitted = true;
        const v     = parseFloat(exitInput.value);
        const valid = !!exitInput.value && !isNaN(v) && v > 0;
        setValidity(exitInput, valid, valid ? '' : 'Exit value must be greater than $0.00.');
        if (!valid) e.preventDefault();
    });

    exitInput.addEventListener('input', function () {
        if (!exitSubmitted) return;
        const v     = parseFloat(exitInput.value);
        const valid = !!exitInput.value && !isNaN(v) && v > 0;
        setValidity(exitInput, valid, valid ? '' : 'Exit value must be greater than $0.00.');
    });
}
