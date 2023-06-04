import React, {useEffect} from 'react';
import './App.css';
import {Link, Route, Routes} from "react-router-dom";
import {HelloWorldControllerService} from "./generated";

function Home() {
    let [helloWorld, setHelloWorld] = React.useState("");

    useEffect(() => {
        HelloWorldControllerService.helloWorld().then(helloWorld => {
            setHelloWorld(helloWorld.message);
        });
    }, []);

    return <div>{helloWorld}</div>;
}

function App() {
    return (<>
            <Routes>
                <Route path="/" element={<Home/>}/>

                <Route path="*" element={<div>
                    <h1>404</h1>
                    <p>Page not found</p>
                    <Link to="/">Go to home</Link>
                </div>}/>
            </Routes>
        </>
    );
}

export default App;
