async function submitLoginForm() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const statusText = document.getElementById("status-text");

    try {
        const response = await fetch('/api/users/login', {
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
            statusText.innerText = "U bent ingelogd, welkom!"
        } else if (response.status === 400) {
            statusText.innerText = "Uw gebruikersnaam en wachtwoord mogen niet leeg zijn."
        } else {
            statusText.innerText = "Uw wachtwoord of gebruikersnaam klopt niet. Probeer het nog een keer."
        }
    } catch (error) {
        console.error('Error during login:', error);
    }
}