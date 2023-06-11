import React from "react";
import {NutritionData} from "../generated";
import RPanel from "../lib/RPanel";
import RDataTable from "../lib/RDataTable";

const ShowNutritionData: React.FC<{ nutritionData: NutritionData }> = ({nutritionData}) => {
    const data = {
        "Calories": nutritionData.calories,
        "Fat": nutritionData.fat,
        "Saturated Fat": nutritionData.saturatedFat,
        "Protein": nutritionData.protein,
        "Sugar": nutritionData.sugar,
        "Salt": nutritionData.salt,
        "Vegetarian": nutritionData.vegetarian,
        "Vegan": nutritionData.vegan
    }

    return (
        <RPanel title="Nutrition per portion">
            <RDataTable data={data} hideEmpty={true}/>
        </RPanel>
    );
}

export default ShowNutritionData;
