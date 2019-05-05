import PageResult from "./PageResult";

export function calculatePage(pageSize, index) {
    return Math.ceil((index + 1) / pageSize);
}

export default class Paging {
    constructor(pageSize) {
        this.pageSize = pageSize;
        this.deletedItemsCount = 0;
    }

    needsToRefetch() {
        return this.deletedItemsCount > 0;
    }

    incrementDeleteCount() {
        ++this.deletedItemsCount;
    }

    /**
     * Vypocitaj parametre pre aktualizaciu strany
     * @param {number} currentPage aktualna strana
     */
    calculateRefetchPage(currentPage) {
        if (this.deletedItemsCount === 0) {
            throw new Error('Tato metoda by sa mala iba volat, ak doslo k zmazaniu elementov');
        }
        // Vypocitaj kolko elementov sa presunie na predchadzajucu stranu
        const presah = this.deletedItemsCount % this.pageSize;
        // Vypocitaj pocet vymazanych stran
        const removedPages = this.calculateDeletedPages();
        // Vypocitaj, kolko elementov bude aktualizacia ignorovat (elementy, ktore su uz stiahnute)
        const ignoreCount = this.pageSize - presah;
        // Sprav poslednu kalkulaciu cisla aktualizacnej stranky a vrat vysledok
        return new PageResult(currentPage - removedPages, this.pageSize, ignoreCount);
    }

    calculateDeletedPages() {
        return Math.floor(this.deletedItemsCount / this.pageSize);
    }

    resetDeleteCount() {
        this.deletedItemsCount = 0;
    }

}