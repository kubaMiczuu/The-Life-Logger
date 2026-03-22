export const formatTime = (range: number, totalSeconds:number):string => {

    let label:string = "";

    if(range === 24) {
        const minutes = Math.floor(totalSeconds / 60);
        const seconds = totalSeconds % 60;

        label = `${minutes}m${seconds}s`;
    }

    else if(range === 7 || (range>27 && range < 32)) {
        const hours = Math.floor(totalSeconds / 3600);
        const minutes = (totalSeconds % 3600)/60;

        label = `${hours}h ${minutes}m`;
    }

    else if(range === 12) {
        const days = Math.floor(totalSeconds / 86400);
        const hours = (totalSeconds % 86400)/3600;

        label = `${days}d ${hours}m`;
    }

    return label;
}