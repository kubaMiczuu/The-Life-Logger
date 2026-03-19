const LoadingPage = () => {
    return (
        <div className="flex flex-col items-center justify-center p-4 w-5/6">
            <div className="bg-[#131316] border border-purple-500/30 p-15 rounded-2xl shadow-[0_0_50px_rgba(168,85,247,0.25)] text-center">
                <h1 className="text-6xl font-black text-white mb-2 border-b-5 rounded-2xl border-purple-500 p-3 pt-0 cursor-default">
                    LIFE<span className="text-purple-500">LOGGER</span>
                </h1>
                <p className="text-zinc-400 text-2xl mt-5">Welcome to your life</p>
                <div className="mt-6 flex gap-3 justify-center">
                    <div className="h-2 w-2 rounded-full bg-purple-500 animate-ping"></div>
                    <span className="text-xs uppercase tracking-widest text-purple-400 font-bold">Loading your data...</span>
                </div>
            </div>
        </div>
    )
}

export default LoadingPage