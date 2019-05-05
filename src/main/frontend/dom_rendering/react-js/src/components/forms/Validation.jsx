import {isBlank, pluralize} from 'util/String';

const predefinedPatterns = {
    email: /(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])/,
    number: /\d+/
}

export function defaultValidation(value, { type, min, max, pattern, validation, required }) {
    let errors = [];
    let length = 0;

    if (value) {
        if (type === 'text' || type === 'email') {
            type = 'text';
            length = value.length;
        }
        else if (type === 'number') {
            length = predefinedPatterns.number.test(value) ? parseInt(value) : min;
        }
        else {
            //throw 'Hodnota sa neda premenit na cislo';
            //Nechaj prejst skuskout
            length = min;
        }
    }
    
    if (length < min || max < length) {
        errors.push({ type: 'OutOfRange', variableType: type, value: length, min: min, max: max, });
    }

    if (required && isBlank(value)) {
        errors.push('Pole nemôže byť prázdne');
    }

    if (pattern) {
        if (!pattern.test(value)) {
            errors.push({ type: 'PatternError', variableType: typeof value, value: value, pattern: pattern });
        }
    }
    else if (predefinedPatterns.hasOwnProperty(type) && !predefinedPatterns[type].test(value)) {
        errors.push(`Pole musi obsahovať ${type}`);
    }

    const userErrors = validation(value);

    if (userErrors) {
        errors.push(userErrors);
    }

    return errors;
}

export function defaultTransformErrors(errors, { patternHelp, transformError }) {
    return errors.map(error => {
        if (error.type === 'OutOfRange' && error.variableType === 'text') {
            return `Pole musí obsahovať ${error.min}${isFinite(error.max) ? ` až ${error.max}` : ''} ${pluralize(isFinite(error.max) ? error.max : error.min, 'znak', 'znaky', 'znakov')}`;
        }

        else if (error.type === 'OutOfRange' && error.variableType === 'number') {
            return `Pole musí obsahovať hodnotu ${error.min}${isFinite(error.max) ? ` až ${error.max}` : ''}`;
        }

        if (error.type === 'PatternError') {
            return patternHelp ? patternHelp : 'Vzor sa nezhoduje';
        }
        else {
            return transformError(error);
        }
    });
}
