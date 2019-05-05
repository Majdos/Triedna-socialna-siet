export function canWrite(permissions) {
    return permissions.includes('WRITE');
}

export function canDelete(permissions) {
    return permissions.includes('ADMIN');
}

export function isUser(permissions) {
    return permissions.includes('READ') || permissions.includes('WRITE');
}

export function isAdmin(permissions) {
    return permissions.includes('ADMIN');;
}

export function isOwner(permissions) {
    return permissions.includes('ROOT');
}