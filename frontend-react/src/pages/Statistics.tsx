
const Statistics = () => {
    return (
        <div className="lg:w-4/5 flex flex-col bg-[#131316] border-2 border-purple-500/30 rounded-2xl shadow-[0_0_50px_rgba(168,85,247,0.25)] text-center">

            <h1 className={`lg:h-[10%] lg:m-[2%] defaultColorFormat p-5 text-4xl border-2 border-purple-500/30 rounded-2xl m-5 font-['JetBrains_Mono',monospace]`}>Let's dive deeper into <span className={`whiteColorFormat`}>your</span> activities!</h1>

            <div className={`flex flex-row h-[90%]`}>

                <div className={`lg:w-1/4 lg:m-[2%] lg:mt-0 lg:h-[97%] border-2 border-purple-500/30 rounded-2xl text-white`}>Processes</div>

                <div className={`lg:w-1/2 lg:m-[2%] lg:mt-0 lg:h-[97%] border-2 border-purple-500/30 rounded-2xl text-white`}>Chart</div>

                <div className={`lg:w-1/4 lg:m-[2%] lg:mt-0 lg:h-[97%] border-2 border-purple-500/30 rounded-2xl text-white`}>Websites</div>

            </div>


        </div>
    )
}
export default Statistics;