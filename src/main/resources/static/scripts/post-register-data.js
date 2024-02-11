async function submitRegisterForm() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const statusText = document.getElementById("status-text");

    try {

        const response = await fetch('/api/users/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                password: password,
            }),
        });
        if (response.ok) {
            statusText.innerText = "U bent geregistreerd, welkom!"
        } else {
            statusText.innerText = "Gebruikersnaam is al in gebruik."
        }
    } catch (error) {
        console.error('Error during login:', error);
    }
}