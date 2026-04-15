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
        <>
            <h1 className={`defaultColorFormat p-5 text-4xl border-2 border-purple-500/30 rounded-2xl m-5 font-['JetBrains_Mono',monospace]`}>Let's see how did <span className={`text-white underline`}>you</span> spent <span className={`text-white underline`}>your</span> day!</h1>

            <div className="flex flex-row justify-center border-purple-500/30 rounded-2xl gap-10">

                <div className="w-3/10 h-30 border-2 border-purple-500/30 rounded-2xl flex flex-col justify-center items-center gap-1">
                    <span className={`text-3xl defaultColorFormat`}><span className={`whiteColorFormat`}>You</span> spent:</span>
                    <span className={`text-2xl font-['JetBrains_Mono',monospace] defaultColorFormat underline`}>{todaySummedTime}</span>
                    <span className={`text-xl font-['JetBrains_Mono',monospace] font-black text-white`}>before screen today... Woah...</span>
                </div>

                <div className="w-3/10 border-2 border-purple-500/30 rounded-2xl flex flex-col justify-center items-center gap-1">
                    <span className={`text-3xl defaultColorFormat`}><span className={`whiteColorFormat`}>You</span> spent the most time on:</span>
                    <span className={`text-2xl font-['JetBrains_Mono',monospace] defaultColorFormat underline`}>{todayTopApplication.processName}</span>
                    <span className={`text-xl font-['JetBrains_Mono',monospace] font-black text-white`}>this application really got you today!</span>
                </div>

                <div className="w-3/10 border-2 border-purple-500/30 rounded-2xl flex flex-col justify-center items-center gap-1">
                    <span className={`text-3xl defaultColorFormat`}><span className={`whiteColorFormat`}>Your</span> category of the day is:</span>
                    <span className={`text-2xl font-['JetBrains_Mono',monospace] defaultColorFormat underline`}>{todayTopCategory.category}</span>
                    <span className={`text-xl font-['JetBrains_Mono',monospace] font-black text-white`}>this category is really something...</span>
                </div>

            </div>
        </>
    )
}

export default HomePageBasicStatistics;