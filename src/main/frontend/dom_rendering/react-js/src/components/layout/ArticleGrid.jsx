import React from 'react';
import PropTypes from 'prop-types';

function calculateOffset(size) {
    return (12 - size) / 2;
}

export default function ArticleGrid(props) {
    const { marginTop, marginBotttom, marginY, children, center, xs, sm, md, lg, xl } = props;

    let { className, colClassName } = props;
    let style = {};

    if (marginY) {
        style.marginTop = marginY;
        style.marginBotttom = marginY;
    }
    else {
        if (marginTop) {
            style.marginTop = marginTop;
        }
        if (marginBotttom) {
            style.marginBotttom = marginBotttom;
        }
    }

    if (center) {
        colClassName += ' d-flex justify-content-center';
    }

    const offsets = `offset-xs-${calculateOffset(xs)} offset-sm-${calculateOffset(sm)} offset-md-${calculateOffset(md)} offset-lg-${calculateOffset(lg)} offset-xl-${calculateOffset(xl)}`;

    if (props.row) {
        return (
            <div className={`row ${className}`} style={style}>
                <div className={`col-sm-${sm} cols-xs-${xs} col-md-${md} offset-lg-2 col-lg-${lg} offset-xl-2 col-xl-${xl} ${offsets} ${colClassName}`} style={style}>
                    {children}
                </div>
            </div>
        );
    }
    else {
        return (
            <div className={`col-sm-${sm} cols-xs-${xs} col-md-${md} col-lg-${lg} col-xl-${xl} ${offsets} ${colClassName}`} style={style}>
                {children}
            </div>
        );
    }
}

ArticleGrid.defaultProps = {
    className: '',
    colClassName: '',
    xs: 12,
    sm: 12,
    md: 12,
    lg: 8,
    xl: 8
};

ArticleGrid.propTypes = {
    children: PropTypes.oneOfType([
        PropTypes.array, PropTypes.element
    ]).isRequired
}