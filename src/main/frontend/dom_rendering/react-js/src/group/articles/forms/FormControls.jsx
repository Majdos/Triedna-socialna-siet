import React from 'react';
import SplitDiv from 'components/SplitDiv';

export default function FormControls(props) {  
    const btnClasses = props.btnClasses ? ' ' + props.btnClasses : '';

    return (
        <SplitDiv 
            leftElements={
                <React.Fragment>
                    <button type="submit" className={`btn btn-primary${btnClasses}`} onClick={props.handleSubmit} disabled={props.processing}>
                        {props.submitText || 'Submit'} { props.submitIcon && <i className={`fa ${props.submitIcon}`}></i> }
                    </button>
                    { props.cancelButton &&
                        <button type="reset" className={`btn btn-danger${btnClasses}`} onClick={props.handleCancel} disabled={props.processing}>
                            Cancel { props.cancelIcon && <i className={`fa ${props.cancelIcon}`}></i> }
                        </button>             
                    }
                </React.Fragment> 
            } 
            rightElements={
                <React.Fragment>
                    {props.children}
                </React.Fragment>
            }
        />
    );  
}
