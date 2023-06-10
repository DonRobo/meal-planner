import React, {useEffect, useState} from 'react';
import {Link, useParams} from 'react-router-dom';
import {Recipe, RecipeControllerService} from "../generated";
import {Col} from "react-bootstrap";
import RPanel from "../lib/RPanel";
import ListIngredients from "./ListIngredients";
import ShowSteps from "./ShowSteps";

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
            <Link to="/recipes">Back</Link>
            <RPanel title={recipe.name}>
                <Col sm={4}>
                    {recipe.description}
                </Col>
                <Col sm={6}>
                    <ListIngredients recipe={recipe}/>
                </Col>
                <Col sm={6}>
                    <ShowSteps recipe={recipe}/>
                </Col>
            </RPanel>
        </div>
    );
};

export default DisplayRecipe;
