import React from "react";
import "./RSpinner.css"
import {Button} from "react-bootstrap";

interface RSpinnerProps {
    value: number;
    min?: number;
    max?: number;
    label?: string;
    onChange: (value: number) => void;
}

const RSpinner: React.FC<RSpinnerProps> = ({value, min, max, label, onChange}) => {
    const handleDecrement = () => {
        if (min === undefined || value > min) {
            onChange(value - 1);
        }
    };

    const handleIncrement = () => {
        if (max === undefined || value < max) {
            onChange(value + 1);
        }
    };

    return (
        <div className="r-spinner d-flex align-items-center">
            {label && <label>{label}</label>}
            <Button variant="outline-primary" size="sm" onClick={handleDecrement}>-</Button>
            <span className="mx-1">{value}</span>
            <Button variant="outline-primary" size="sm" onClick={handleIncrement}>+</Button>
        </div>
    );
}
export default RSpinner;
