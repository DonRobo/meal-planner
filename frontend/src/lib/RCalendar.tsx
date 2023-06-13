import React from "react";
import {DateTime} from "luxon";
import {Col, Row} from "react-bootstrap";

import "./RCalendar.css";

interface RCalendarProps {
    start: DateTime;
    days: number;
    children: React.ReactNode;
}

const RCalendar: React.FC<RCalendarProps> = (props) => {
    const childrenArray = React.Children.toArray(props.children);

    return (
        <Row className="calendar">
            {
                Array.from(Array(props.days).keys()).map((i) => {
                    return <Col key={i}>
                        <div className="calendar-header">{props.start.plus({days: i}).toLocaleString({
                            weekday: "long",
                            day: "2-digit",
                            month: "2-digit"
                        })}</div>
                        <div>
                            {childrenArray[i]}
                        </div>
                    </Col>
                })
            }
        </Row>
    );
}

export default RCalendar;
