export interface statsFields {
    processName: string,
    label: string,
    durationSeconds: number,
    category: string,
    domain: string
}

export interface fetchReturnStats {
    processStats: statsFields[];
    categoryStats: statsFields[];
    browserStats: statsFields[];
    timeStats: number[];
}