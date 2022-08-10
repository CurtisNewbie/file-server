export interface NLink {
  base: string;
  route: string;
  name: string;
  permitRoles: Set<string>;
}

const anyRoles: Set<string> = new Set<string>()
  .add("guest")
  .add("admin")
  .add("user");
const adminOnly: Set<string> = new Set<string>().add("admin");

const links: NLink[] = [
  {
    base: "file-service",
    route: "/home-page",
    name: "Home",
    permitRoles: anyRoles,
  },
  {
    base: "file-service",
    route: "/manage-extension",
    name: "Manage File Extension",
    permitRoles: adminOnly,
  },
  {
    base: "file-service",
    route: "/manage-fsgroup",
    name: "Manage FsGroup",
    permitRoles: adminOnly,
  },
  {
    base: "file-service",
    route: "/manage-tasks",
    name: "Manage Task",
    permitRoles: adminOnly,
  },
  {
    base: "file-service",
    route: "/task-history",
    name: "Task History",
    permitRoles: adminOnly,
  },
];

export function selectLinks(base: string, role: string): NLink[] {
  if (!role) return [];
  return links.filter((v, i, a) =>
    v.base === base && v.permitRoles.has(role) ? v : null
  );
}
