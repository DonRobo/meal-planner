import React, {MouseEventHandler, useEffect} from "react";
import RCalendar from "../lib/RCalendar";
import {DateTime} from "luxon";
import {Col} from "react-bootstrap";
import "./MealPlanner.css";
import {PlannedMealControllerService} from "../generated";

interface Meal {
    name: string;
}

const MealPlanner: React.FC = () => {
    const dayCount = 7;

    const [meals, setMeals] = React.useState<Meal[][]>(Array.from({length: dayCount}, () => []));
    const addMeal = (day: number, newMeal: Meal) => {
        if (day < 0 || day >= dayCount) throw new Error("Invalid day");

        meals[day].push(newMeal);
        setMeals([...meals]);
    };

    const updateMeal = (day: number, mealIndex: number, updatedMeal: Meal) => {
        if (day < 0 || day >= dayCount) throw new Error("Invalid day");

        setMeals((prevMeals) => {
            const newMeals = [...prevMeals];
            newMeals[day][mealIndex] = updatedMeal;
            return newMeals;
        });
    };
    const removeMeal = (day: number, mealIndex: number) => {
        if (day < 0 || day >= dayCount) throw new Error("Invalid day");
        if (mealIndex < 0 || mealIndex >= meals[day].length) throw new Error("Invalid meal index");

        setMeals((prevMeals) => {
            const newMeals = [...prevMeals];
            newMeals[day].splice(mealIndex, 1);
            return newMeals;
        });
    };

    // Fetch meals from backend using PlannedMealControllerService
    const fetchMeals = (day: DateTime) => {
        const meals = PlannedMealControllerService.plannedMealsForDay(day.toISODate()!!);
        meals.then((meals) => {
            setMeals((prevMeals) => {
                const newMeals = [...prevMeals];
                newMeals[day.diff(DateTime.now(), 'days').days] = meals.map((meal) => {
                    return {name: meal.data.desiredCalories?.toString() || "Undefined"};
                });
                return newMeals;
            });
        });
    }

    useEffect(() => {
        const today = DateTime.now();
        for (let i = 0; i < dayCount; i++) {
            fetchMeals(today.plus({days: i}));
        }
    }, []);

    return (
        <div>
            <RCalendar start={DateTime.now()} days={dayCount}>
                {
                    Array.from({length: dayCount}, (_, i) => {
                        return <DayContent
                            key={i}
                            meals={meals[i]}
                            addMeal={
                                function (meal: Meal) {
                                    addMeal(i, meal);
                                }
                            }
                            updateMeal={
                                function (mealIndex: number, meal: Meal) {
                                    updateMeal(i, mealIndex, meal);
                                }
                            }
                            removeMeal={
                                function (mealIndex: number) {
                                    removeMeal(i, mealIndex);
                                }
                            }
                        />
                    })
                }
            </RCalendar>
        </div>
    );
}

interface DayContentProps {
    meals: Meal[];
    addMeal: (meal: Meal) => void;
    updateMeal: (mealIndex: number, meal: Meal) => void;
    removeMeal: (mealIndex: number) => void;
}

const DayContent: React.FC<DayContentProps> = (props) => {
    const addNewMeal: MouseEventHandler<HTMLDivElement> = (e) => {
        props.addMeal({name: "New meal"});
    }

    return <Col>
        {
            props.meals.map((meal, i) => {
                return <div key={i}>{meal.name}</div>
            })
        }
        <div className="add-new-meal" onClick={addNewMeal}>Add new meal</div>
    </Col>;
}
export default MealPlanner;
