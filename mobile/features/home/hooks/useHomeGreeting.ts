import { useAuthStore } from "@/store/authStore";

export function useHomeGreeting() {
  const user = useAuthStore((s) => s.user);
  const hour = new Date().getHours();

  let greeting: string;
  if (hour < 12) {
    greeting = "Good morning,";
  } else if (hour < 17) {
    greeting = "Good afternoon,";
  } else {
    greeting = "Good evening,";
  }

  return {
    greeting,
    firstName: user?.firstName ?? "there",
    estateName: user?.estateName ?? "",
  };
}
