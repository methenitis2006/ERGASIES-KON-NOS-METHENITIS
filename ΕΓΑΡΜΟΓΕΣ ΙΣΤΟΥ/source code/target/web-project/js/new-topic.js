const topicForm = document.getElementById('topicForm');
const topicResult = document.getElementById('topicResult');

function showTopicResult(message, ok) {
  topicResult.textContent = message;
  topicResult.classList.remove('hidden');
  topicResult.classList.toggle('success', ok);
  topicResult.classList.toggle('error', !ok);
}

topicForm.addEventListener('submit', async (event) => {
  event.preventDefault();

  const name = topicForm.name.value.trim();
  const description = topicForm.description.value.trim();

  if (!name || !description) {
    showTopicResult('Name and description are required.', false);
    return;
  }

  try {
    const response = await fetch('api/topics', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ name, description })
    });

    const data = await response.json();
    showTopicResult(data.message || 'Request completed.', !!data.success);
    if (data.success) {
      topicForm.reset();
    }
  } catch (error) {
    showTopicResult('Unexpected error while saving topic.', false);
  }
});
