export function redirect(url, asLink = true) {
    return asLink ? (window.location.href = url) : window.location.replace(url);
}

export function formatUrlForStomp(url) {
    const templateIndex = url.indexOf('{');
    if (templateIndex !== -1) {
        url = url.substring(0, templateIndex);
    }
    return url;
}

export function getGroupImage(imageName, groupName) {
    return `/images/${groupName || 'public'}/${imageName}`;
}

export function removeQuery(url) {
    if (url.indexOf('?' !== -1)) {
        return url.split('?')[0];
    }
    return url;
}
