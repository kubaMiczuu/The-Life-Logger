import { NavLink } from "react-router-dom";
import type {ReactNode} from "react";

interface SidebarItemProps {
    name: string;
    to: string;
    icon: ReactNode;
}

const SidebarItem = ({ name, to, icon }: SidebarItemProps) => {
    return (
        <NavLink
            to={to}
            className={({ isActive }) => `flex flex-row items-center transition-all duration-300 pb-4 hover:scale-110 hover:text-purple-500
        ${isActive
                ? "text-purple-500 underline underline-offset-8 decoration-2]"
                : "text-white no-underline"}
    `}
        >
            <span className="pr-5">{icon}</span>
            <span className="font-medium">{name}</span>
        </NavLink>
    );
};

export default SidebarItem;