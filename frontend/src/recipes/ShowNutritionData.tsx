import React from "react";
import {NutritionData} from "../generated";
import RPanel from "../lib/RPanel";
import RDataTable from "../lib/RDataTable";

const ShowNutritionData: React.FC<{ nutritionData: NutritionData }> = ({nutritionData}) => {
    const addUnit = (value: any, unit: string) => {
        if (value === null || value === undefined) return undefined;

        return value + " " + unit;
    }

    const data = {
        "Calories": addUnit(nutritionData.calories, "kcal"),
        "Fat": addUnit(nutritionData.fat, "g"),
        "Saturated Fat": addUnit(nutritionData.saturatedFat, "g"),
        "Protein": addUnit(nutritionData.protein, "g"),
        "Sugar": addUnit(nutritionData.sugar, "g"),
        "Salt": addUnit(nutritionData.salt, "g"),
        "Vegetarian": nutritionData.vegetarian,
        "Vegan": nutritionData.vegan
    };

    return (
        <RPanel title="Nutrition per portion">
            <RDataTable data={data} hideEmpty={true}/>
        </RPanel>
    );
}

export default ShowNutritionData;
