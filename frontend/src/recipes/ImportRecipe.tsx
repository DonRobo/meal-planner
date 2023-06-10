import React from "react";
import RTextInput from "../lib/RTextInput";
import {Button} from "react-bootstrap";
import {RecipeControllerService} from "../generated";

const ImportRecipe: React.FC<{ onChange: () => void }> = (props) => {
    const [url, setUrl] = React.useState<string>("");
    const [loading, setLoading] = React.useState<boolean>(false);

    const doImport = () => {
        setLoading(true);
        RecipeControllerService.importRecipe(url).then(() => {
            props.onChange();
            setUrl("");
            setLoading(false);
        }).catch((error) => {
            console.error(error); // add error handling here
            setLoading(false);
        });
    }

    return (
        <div className="d-flex flex-row p-1">
            <RTextInput value={url} onChange={setUrl}/>
            <Button disabled={loading} onClick={doImport}>
                {loading ? "Importing..." : "Import"}
            </Button>
        </div>
    );
}
export default ImportRecipe;
