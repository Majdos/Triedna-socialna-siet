export function capitalize(string) {
    return string.split(/\s+/).map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()).join(' ');
}

export function pluralize(count, ...forms) {
    if (count === 1) {
        return forms[0];
    }
    if (1 < count && count < 5) {
        return forms[1];
    }
    else {
        return forms[2];
    }
}

export function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}