export function formatClassName(...classes) {
    return classes.filter(x => x != null).join(' ');
}