import axios from "axios";

import type {fetchReturnStats} from "./types.ts";

interface fetchProps {
    range: string,
    startDate: string,
    endDate: string
}

export const fetchData = async ({range, startDate, endDate}: fetchProps):Promise<fetchReturnStats | null> => {
    try {
        const response = await axios.get<fetchReturnStats>('http://localhost:8080/api/stats/summary', {
            params: {range: range, startDate: startDate, endDate: endDate}
        })

        return response.data;
    }
    catch(err) {
        console.error(err);
        return null;
    }
}