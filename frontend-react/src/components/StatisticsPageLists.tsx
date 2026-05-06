import {formatTime} from "../utils/formatTime.ts";
import type {statsFields} from "../utils/types.ts";

interface statLists {
    title: string;
    stats: statsFields[];
}

const StatisticsPageLists = ({title, stats}:statLists) => {
    return (
        <div className={`lg:w-1/4 lg:m-[2%] lg:mt-0 lg:h-[97%] border-2 border-purple-500/30 rounded-2xl text-white`}>
            <span className={`text-3xl defaultColorFormat font-['JetBrains_Mono',monospace]`}>{title}</span>

            <div className={`grid grid-cols-[2fr_1fr] text-xl m-5`}>
                <span className={`text-left text-2xl whiteColorFormat `}>Name:</span>
                <span className={`text-right text-2xl mb-2 whiteColorFormat`}>Time:</span>

                {stats.map((item, index) => (
                    <>
                                    <span className={`text-left truncate defaultColorFormat font-['JetBrains_Mono',monospace]`} key={`name-${index}`}>{
                                        item.processName != null ? item.processName : "other"
                                    }</span>
                        <span className={`text-right`} key={`time-${index}`}>{
                            item.durationSeconds > 3600 ? formatTime(7, item.durationSeconds) : formatTime(24, item.durationSeconds)
                        }</span>
                    </>
                ))}
            </div>
        </div>
    )
}

export default StatisticsPageLists;