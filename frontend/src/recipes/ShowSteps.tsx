import React from "react";
import {Recipe} from "../generated";
import RPanel from "../lib/RPanel";

import './ShowSteps.css';

interface ShowStepsProps {
    recipe: Recipe;
}

const ShowSteps: React.FC<ShowStepsProps> = ({recipe}) => {
    return <RPanel title="Steps">
        <ol className="step-list">
            {recipe.steps.map((step, index) => (
                <li key={index}>
                    {step.text}
                </li>
            ))}
        </ol>
    </RPanel>
}

export default ShowSteps;
