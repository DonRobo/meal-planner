import React from "react";
import RCalendar from "../lib/RCalendar";
import {DateTime} from "luxon";

const MealPlanner: React.FC = () => {
    return (
        <div>
            <RCalendar start={DateTime.now()} days={7}>
                <div>Day 1 content</div>
                <div>Day 2 content</div>
                <div>Day 3 content</div>
                <div>Day 4 content</div>
                <div>Day 5 content</div>
                <div>Day 6 content</div>
                <div>Day 7 content</div>
            </RCalendar>
        </div>
    );
}

export default MealPlanner;
