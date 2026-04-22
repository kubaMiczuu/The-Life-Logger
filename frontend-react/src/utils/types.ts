export interface processStatsFields {
    processName: string,
    label: string,
    durationSeconds: number,
    category: string,
    domain: string
}

export interface categoryStatsFields {
    processName: string,
    label: string,
    durationSeconds: number,
    category: string,
    domain: string
}

export interface browserStatsFields {
    processName: string,
    label: string,
    durationSeconds: number,
    category: string,
    domain: string
}

export interface fetchReturnStats {
    processStats: processStatsFields[];
    categoryStats: categoryStatsFields[];
    browserStats: browserStatsFields[];
    timeStats: number[];
}