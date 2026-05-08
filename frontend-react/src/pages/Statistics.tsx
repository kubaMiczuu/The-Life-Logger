import {useEffect, useState} from "react";
import SystemStatus from "./SystemStatus.tsx";
import {fetchData} from "../utils/fetchData.ts";

import type {statsFields} from "../utils/types.ts";
import StatisticsPageLists from "../components/StatisticsPageLists.tsx";

const Statistics = () => {

    const [isLoading, setIsLoading] = useState(true);

    const [range, setRange] = useState<string>("daily");
    const [startDate, setStartDate] = useState<string>("");
    const [endDate, setEndDate] = useState<string>("");

    const [processStats, setProcessStats] = useState<statsFields[]>([]);
    const [categoryStats, setCategoryStats] = useState<statsFields[]>([]);
    const [browserStats, setBrowserStats] = useState<statsFields[]>([]);
    const [timeStats, setTimeStats] = useState<number[]>([]);

    useEffect(() => {
        fetchData({range: range, startDate: startDate, endDate: endDate})
            .then(response => {
                if(response == null) return;

                setProcessStats(response.processStats);
                setCategoryStats(response.categoryStats);
                setBrowserStats(response.browserStats);
                setTimeStats(response.timeStats);

                setIsLoading(false);
            })
            .catch(err => {
                console.log(err)
            });
    }, [])

    if (isLoading) return <SystemStatus message={"Loading your data..."} />;

    if(processStats.length > 0 && categoryStats.length > 0) {
        return (
            <div className="lg:w-4/5 flex flex-col bg-[#131316] border-2 border-purple-500/30 rounded-2xl shadow-[0_0_50px_rgba(168,85,247,0.25)] text-center">

                <h1 className={`lg:h-[10%] lg:m-[2%] defaultColorFormat p-5 text-4xl border-2 border-purple-500/30 rounded-2xl m-5 font-['JetBrains_Mono',monospace]`}>Let's dive deeper into <span className={`whiteColorFormat`}>your</span> activities!</h1>

                <div className={`flex flex-row h-[90%]`}>

                    <StatisticsPageLists title="Processes" stats={processStats} />

                    <div className={`lg:w-1/2 lg:m-[2%] lg:mt-0 lg:h-[97%] flex flex-col lg:gap-5`}>

                        <div className={`w-full lg:mt-0 lg:h-[15%] border-2 border-purple-500/30 rounded-2xl text-white`}>
                            <span className={`text-2xl defaultColorFormat font-['JetBrains_Mono',monospace]`}>Time range</span>
                        </div>

                        <div className={`w-full lg:h-[50%] border-2 border-purple-500/30 rounded-2xl text-white`}>

                        </div>

                        <div className={`w-full lg:mb-0 lg:h-[30%] border-2 border-purple-500/30 rounded-2xl text-white`}>

                        </div>

                    </div>

                    <StatisticsPageLists title="Websites" stats={browserStats} />

                </div>

            </div>
        )
    }

    return <SystemStatus message={"There is no data to show..."} />

}
export default Statistics;