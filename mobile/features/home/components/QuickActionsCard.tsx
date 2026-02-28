import { Card } from "@/components/ui/Card";
import { ChevronRight, KeyRound, Plus, Users } from "lucide-react-native";
import React from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";

const PRIMARY_DARK = "#00483C";
const MEDIUM_GRAY = "#E0E0E0";
const ICON_BG = "#E8F5E9";

interface QuickActionsCardProps {
  onManageHousehold: () => void;
  onActiveCodes: () => void;
  onNewAccessCode: () => void;
}

const actions = [
  { label: "Manage Household", Icon: Users, key: "manage" as const },
  { label: "Active Codes", Icon: KeyRound, key: "active" as const },
  { label: "New Access Code", Icon: Plus, key: "new" as const },
];

export function QuickActionsCard({
  onManageHousehold,
  onActiveCodes,
  onNewAccessCode,
}: QuickActionsCardProps) {
  const handlers = {
    manage: onManageHousehold,
    active: onActiveCodes,
    new: onNewAccessCode,
  };

  return (
    <Card variant="elevated" padding="none" style={styles.card}>
      {actions.map((action, index) => (
        <React.Fragment key={action.key}>
          <TouchableOpacity
            style={styles.row}
            onPress={handlers[action.key]}
            activeOpacity={0.7}
          >
            <View style={styles.iconContainer}>
              <action.Icon size={18} color={PRIMARY_DARK} />
            </View>
            <Text style={styles.label}>{action.label}</Text>
            <ChevronRight size={18} color={MEDIUM_GRAY} />
          </TouchableOpacity>
          {index < actions.length - 1 && <View style={styles.separator} />}
        </React.Fragment>
      ))}
    </Card>
  );
}

const styles = StyleSheet.create({
  card: {
    marginTop: 15,
  },
  row: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 16,
    paddingVertical: 16,
  },
  iconContainer: {
    width: 36,
    height: 36,
    borderRadius: 8,
    backgroundColor: ICON_BG,
    alignItems: "center",
    justifyContent: "center",
  },
  label: {
    flex: 1,
    marginLeft: 12,
    fontSize: 15,
    fontWeight: "500",
    color: "#000000",
  },
  separator: {
    height: StyleSheet.hairlineWidth,
    backgroundColor: MEDIUM_GRAY,
    marginHorizontal: 16,
  },
});
