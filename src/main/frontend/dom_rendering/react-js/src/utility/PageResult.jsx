export default class PageResult {
    constructor(page, size, ignoreCount) {
        this.page = page;
        this.size = size;
        this.ignoreCount = ignoreCount;
    }

    getPageNumber() {
        return this.page;
    }

    getPageSize() {
        return this.size;
    }

    getIgnoreCount() {
        return this.ignoreCount;
    }
}