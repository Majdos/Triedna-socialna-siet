import React from 'react';
export default function InputDiv({children, className, ...props}) {
    return <div className={className}>
        {React.Children.map(children, child => child != null ? React.cloneElement(child, props) : null)}
    </div>
}