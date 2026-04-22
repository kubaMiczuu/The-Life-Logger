import type {processStatsFields} from "../utils/types.ts";


interface recentActivityStats {
    todayProcessStats: processStatsFields[];
}

const HomePageRecentActivity = ({todayProcessStats}:recentActivityStats) => {
    return(
        <div className={`lg:m-[2%] lg:h-[10%] border-2 border-purple-500/30 rounded-2xl defaultColorFormat`}>

            <span className={`text-center text-2xl font-['JetBrains_Mono',monospace]`}><span className={`whiteColorFormat`}>Recent</span> active processes</span>

            <div className={`flex flex-row text-lg flex-wrap justify-center items-center gap-x-12 pt-1`}>
                {todayProcessStats.slice(0,5).map((process, index:number) =>
                    <span className={`font-['JetBrains_Mono',monospace] font-black whitespace-nowrap`}><span className={`whiteColorFormat`}>{index+1}.</span> {process.processName} </span>
                )}
            </div>

        </div>
    )
}

export default HomePageRecentActivity;