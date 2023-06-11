import React from "react";

import './RDataTable.css';
import {Col, Row} from "react-bootstrap";

interface RDataTableProps {
    data: { [key: string]: string | number | boolean | null | undefined };
    hideEmpty?: boolean;
}

const RDataTable: React.FC<RDataTableProps> = ({data, hideEmpty}) => {
    if (hideEmpty && Object.values(data).filter(val => val).length === 0) {
        return (
            <div>No data</div>
        );
    }

    return (
        <div className="light-border">
            {
                Object.keys(data).map((key) => {
                    const d = data[key];
                    if (!hideEmpty || (d && d.toString().length > 0)) {
                        return (
                            <Row key={key}>
                                <Col sm={6} className="fw-bold">{key}</Col>
                                <Col sm={6}>{data[key]?.toString() ?? ""}</Col>
                            </Row>
                        );
                    } else {
                        return null;
                    }
                })
            }
        </div>
    );
}

export default RDataTable;
