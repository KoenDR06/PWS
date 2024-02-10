async function submitLoginForm() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch('/api/login', {
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
            const data = await response.json();
            console.log('Login successful. Token:', data.token);
            // You can redirect or perform other actions after a successful login
        } else {
            console.error('Login failed.');
            // Handle failed login (e.g., display an error message)
        }
    } catch (error) {
        console.error('Error during login:', error);
        // Handle errors (e.g., display an error message)
    }
}