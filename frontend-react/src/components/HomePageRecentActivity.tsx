interface processStat {
    processName: string;
}

interface recentActivityStats {
    todayProcessStats: processStat[];
}

const HomePageRecentActivity = ({todayProcessStats}:recentActivityStats) => {
    return(
        <div className={`m-5 h-1/8 border-2 border-purple-500/30 rounded-2xl defaultColorFormat`}>

            <span className={`text-center text-2xl font-['JetBrains_Mono',monospace]`}><span className={`whiteColorFormat`}>Recent</span> active processes</span>

            <div className={`flex flex-row text-lg flex-wrap justify-center items-center gap-x-12 gap-y-2 p-5`}>
                {todayProcessStats.slice(0,5).map((process, index:number) =>
                    <span className={`font-['JetBrains_Mono',monospace] font-black whitespace-nowrap`}><span className={`whiteColorFormat`}>{index+1}.</span> {process.processName} </span>
                )}
            </div>

        </div>
    )
}

export default HomePageRecentActivity;