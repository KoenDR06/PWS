async function likeRecipe(userId, recipeId) {
    const svg = document.getElementById(`heart-image-${recipeId}`);

    console.log(typeof userId)
    console.log(typeof recipeId)

    try {
        const response = await fetch('/api/users/like', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userId: userId,
                recipeId: recipeId,
            }),
        });

        if (response.ok) {
            svg.src = "/static/images/liked.svg"
        }
    } catch (error) {
        console.error(error);
    }
}