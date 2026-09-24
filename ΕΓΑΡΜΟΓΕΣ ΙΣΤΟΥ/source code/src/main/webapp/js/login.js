const loginForm = document.getElementById('loginForm');
const loginError = document.getElementById('loginError');

function showLoginError(message) {
  loginError.textContent = message;
  loginError.classList.remove('hidden');
}

loginForm.addEventListener('submit', (event) => {
  const username = loginForm.username.value.trim();
  const password = loginForm.password.value;

  loginError.classList.add('hidden');
  loginError.textContent = '';

  if (!username || !password) {
    event.preventDefault();
    showLoginError('Username and password are required.');
    return;
  }
});

const params = new URLSearchParams(window.location.search);
if (params.get('error') === 'missing') {
  showLoginError('Please type both username and password.');
}
if (params.get('error') === 'invalid') {
  showLoginError('Invalid username or password.');
}
