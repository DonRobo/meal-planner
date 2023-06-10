import React, {ReactNode} from 'react';

import './RPanel.css';
import {Row} from "react-bootstrap";

interface PanelProps {
    title?: string;
    children: ReactNode;
}

const RPanel: React.FC<PanelProps> = ({title, children}) => {
    return (
        <div className="panel">
            {title && (
                <div className="panel-header">
                    {title}
                </div>
            )}
            <Row>
                {children}
            </Row>
        </div>
    );
};

export default RPanel;
