interface basicStatistics {
    todaySummedTime: string;
    todayTopApplication: {
        processName: string;
    };
    todayTopCategory: {
        category: string;
    };
}

const HomePageBasicStatistics = ({todaySummedTime, todayTopApplication, todayTopCategory}:basicStatistics) => {
    return(
        <div className="lg:h-[20%] lg:m-[2%] flex flex-row justify-center border-purple-500/30 rounded-2xl gap-10">

            <div className="lg:w-3/10 p-1 border-2 border-purple-500/30 rounded-2xl flex flex-col justify-center items-center gap-1">
                <span className={`text-2xl defaultColorFormat`}><span className={`whiteColorFormat`}>You</span> spent:</span>
                <span className={`text-xl font-['JetBrains_Mono',monospace] defaultColorFormat underline`}>{todaySummedTime}</span>
                <span className={`text-lg font-['JetBrains_Mono',monospace] font-black text-white`}>before screen today... Woah...</span>
            </div>

            <div className="lg:w-3/10 p-1 border-2 border-purple-500/30 rounded-2xl flex flex-col justify-center items-center gap-1">
                <span className={`text-2xl defaultColorFormat`}><span className={`whiteColorFormat`}>You</span> spent time mostly on:</span>
                <span className={`text-xl font-['JetBrains_Mono',monospace] defaultColorFormat underline`}>{todayTopApplication.processName}</span>
                <span className={`text-lg font-['JetBrains_Mono',monospace] font-black text-white`}>this application really got you today!</span>
            </div>

            <div className="lg:w-3/10 p-1 border-2 border-purple-500/30 rounded-2xl flex flex-col justify-center items-center gap-1">
                <span className={`text-2xl defaultColorFormat`}><span className={`whiteColorFormat`}>Your</span> top category today is:</span>
                <span className={`text-xl font-['JetBrains_Mono',monospace] defaultColorFormat underline`}>{todayTopCategory.category}</span>
                <span className={`text-lg font-['JetBrains_Mono',monospace] font-black text-white`}>this category is really something...</span>
            </div>

        </div>
    )
}

export default HomePageBasicStatistics;