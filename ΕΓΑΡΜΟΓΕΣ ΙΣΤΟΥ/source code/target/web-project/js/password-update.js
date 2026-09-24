const form = document.getElementById('passwordForm');
const errorBox = document.getElementById('passwordError');

function showError(message) {
  errorBox.textContent = message;
  errorBox.classList.remove('hidden');
}

form.addEventListener('submit', (event) => {
  const username = form.username.value.trim();
  const currentPassword = form.currentPassword.value;
  const p1 = form.newPassword1.value;
  const p2 = form.newPassword2.value;
  const p3 = form.newPassword3.value;

  errorBox.classList.add('hidden');
  errorBox.textContent = '';

  if (!username || !currentPassword || !p1 || !p2 || !p3) {
    event.preventDefault();
    showError('Please fill in all five fields.');
    return;
  }

  if (p1 !== p2 || p1 !== p3) {
    event.preventDefault();
    showError('The new password must be identical in all three fields.');
    return;
  }

  if (!/^[A-Za-z0-9]{7,}$/.test(p1)) {
    event.preventDefault();
    showError('The new password must have only letters and digits and be longer than 6 characters.');
    return;
  }

  if (p1 === currentPassword) {
    event.preventDefault();
    showError('The new password must be different from the existing one.');
  }
});
