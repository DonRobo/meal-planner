import React, {JSX, useEffect} from "react";
import {RecipeControllerService, SparseRecipe} from "../generated";
import {Link} from "react-router-dom";

export function RecipeList(): JSX.Element {
    let [recipes, setRecipes] = React.useState<SparseRecipe[]>([]);

    useEffect(() => {
        RecipeControllerService.listRecipes().then((recipes) => {
            setRecipes(recipes);
        });
    }, []);


    return <div>
        <h2>Recipes</h2>
        <ul>
            {recipes.map((recipe) => {
                return <li key={recipe.id}><Link to={`/recipes/${recipe.id}`}>{recipe.name}</Link></li>;
            })}
        </ul>
    </div>;
}
