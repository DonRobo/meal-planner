import React, {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import {Recipe, RecipeControllerService} from "../generated";

const DisplayRecipe: React.FC = () => {
    const {id} = useParams<{ id: string }>(); // Access the recipe ID from the URL parameter
    const [recipe, setRecipe] = useState<Recipe | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (id != null) {
            RecipeControllerService.getRecipe(parseInt(id, 10)).then((recipe) => {
                setRecipe(recipe);
                setLoading(false);
            });
        }
    }, [id]);

    if (loading) {
        return <div>Loading recipe details...</div>;
    }

    if (!recipe) {
        return <div>Error fetching recipe details</div>;
    }

    return (
        <div>
            <h1>Recipe Details</h1>
            <h2>{recipe.name}</h2>
            <p>{recipe.description}</p>
        </div>
    );
};

export default DisplayRecipe;
