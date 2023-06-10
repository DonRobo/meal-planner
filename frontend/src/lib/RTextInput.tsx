import React from "react";
import "./RSpinner.css"

interface RTextInputProps {
    value: string;
    label?: string;
    onChange: (value: string) => void;
}

const RTextInput: React.FC<RTextInputProps> = (props) => {
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        props.onChange(event.target.value);
    };

    return (
        <div className="r-text-input d-flex align-items-center">
            {props.label && <label>{props.label}</label>}
            <input type="text" value={props.value} onChange={handleChange}/>
        </div>
    );
}
export default RTextInput;
