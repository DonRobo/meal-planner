import React, {JSX} from 'react';
import './App.css';
import {Link, Route, Routes} from "react-router-dom";
import {RecipeList} from "./recipes/RecipeList";
import DisplayRecipe from "./recipes/DisplayRecipe";

function Home(): JSX.Element {
    return <div>
        <h2>Links</h2>
        <ul>
            <li><Link to="/recipes">Recipes</Link></li>
        </ul>
    </div>;
}

function App() {
    return (<>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/recipes" element={<RecipeList/>}/>
                <Route path="/recipes/:id" element={<DisplayRecipe/>}/>

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
