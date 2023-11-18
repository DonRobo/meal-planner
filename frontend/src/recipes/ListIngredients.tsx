import React from "react";
import {Recipe} from "../generated";
import RPanel from "../lib/RPanel";
import {Col} from "react-bootstrap";
import RSpinner from "../lib/RSpinner";

const ListIngredients: React.FC<{ recipe: Recipe }> = ({recipe}) => {
    const [portions, setPortions] = React.useState(1);

    return <RPanel title="Ingredients">
        <Col sm={12}>
            <RSpinner label="Portions" value={portions} onChange={setPortions} min={1}/>
        </Col>
        {recipe.ingredients!!.map((ingredient) => (
            <Col sm={12} lg={6} key={ingredient.ingredient.id}>
                {ingredient.ingredient.name} ({ingredient.quantity * portions} {ingredient.unit})
            </Col>
        ))}
    </RPanel>;
}

export default ListIngredients;
