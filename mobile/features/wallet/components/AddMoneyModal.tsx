import React from "react";
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  Pressable,
} from "react-native";
import { X, Copy, Building2 } from "lucide-react-native";
import * as Clipboard from "expo-clipboard";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { AccountDetails } from "../types";

interface AddMoneyModalProps {
  visible: boolean;
  onClose: () => void;
  accountDetails: AccountDetails | undefined;
}

function DetailRow({
  label,
  value,
  colors,
  showSeparator = true,
}: {
  label: string;
  value: string;
  colors: (typeof Colors)["light"];
  showSeparator?: boolean;
}) {
  const handleCopy = async () => {
    await Clipboard.setStringAsync(value.replace(/\s/g, ""));
  };

  return (
    <>
      <View style={detailStyles.row}>
        <View style={detailStyles.info}>
          <Text style={[detailStyles.label, { color: colors.textTertiary }]}>
            {label}
          </Text>
          <Text style={[detailStyles.value, { color: colors.text }]}>
            {value}
          </Text>
        </View>
        <TouchableOpacity onPress={handleCopy} hitSlop={8}>
          <Copy size={18} color={colors.textTertiary} />
        </TouchableOpacity>
      </View>
      {showSeparator && (
        <View
          style={[detailStyles.separator, { backgroundColor: colors.border }]}
        />
      )}
    </>
  );
}

const detailStyles = StyleSheet.create({
  row: {
    flexDirection: "row",
    alignItems: "center",
    paddingVertical: 14,
    paddingHorizontal: 16,
  },
  info: {
    flex: 1,
  },
  label: {
    fontSize: 12,
    marginBottom: 2,
  },
  value: {
    fontSize: 15,
    fontWeight: "600",
  },
  separator: {
    height: StyleSheet.hairlineWidth,
    marginHorizontal: 16,
  },
});

export function AddMoneyModal({
  visible,
  onClose,
  accountDetails,
}: AddMoneyModalProps) {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  if (!accountDetails) return null;

  const rows: { label: string; value: string }[] = [
    { label: "Account holder", value: accountDetails.accountHolder },
    { label: "Account number", value: accountDetails.accountNumber },
    { label: "Bank name", value: accountDetails.bankName },
  ];

  return (
    <Modal
      visible={visible}
      transparent
      animationType="slide"
      onRequestClose={onClose}
    >
      <Pressable style={styles.overlay} onPress={onClose}>
        <Pressable
          style={[styles.sheet, { backgroundColor: colors.background }]}
          onPress={(e) => e.stopPropagation()}
        >
          <TouchableOpacity
            style={styles.closeButton}
            onPress={onClose}
            hitSlop={8}
          >
            <X size={22} color={colors.text} />
          </TouchableOpacity>

          <View style={styles.iconContainer}>
            <View style={[styles.iconCircle, { backgroundColor: "#E8F5E9" }]}>
              <Building2 size={32} color="#05C756" />
            </View>
          </View>

          <Text style={[styles.title, { color: colors.text }]}>Add money</Text>
          <Text style={[styles.subtitle, { color: colors.textTertiary }]}>
            Transfer to the account number below
          </Text>

          <View
            style={[styles.detailCard, { backgroundColor: colors.card, borderColor: colors.border }]}
          >
            {rows.map((row, index) => (
              <DetailRow
                key={row.label}
                label={row.label}
                value={row.value}
                colors={colors}
                showSeparator={index < rows.length - 1}
              />
            ))}
          </View>
        </Pressable>
      </Pressable>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.5)",
    justifyContent: "flex-end",
  },
  sheet: {
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    paddingHorizontal: 20,
    paddingTop: 16,
    paddingBottom: 40,
  },
  closeButton: {
    alignSelf: "flex-end",
    padding: 4,
  },
  iconContainer: {
    alignItems: "center",
    marginTop: 8,
  },
  iconCircle: {
    width: 64,
    height: 64,
    borderRadius: 32,
    alignItems: "center",
    justifyContent: "center",
  },
  title: {
    fontSize: 20,
    fontWeight: "700",
    textAlign: "center",
    marginTop: 16,
  },
  subtitle: {
    fontSize: 14,
    textAlign: "center",
    marginTop: 8,
    marginBottom: 20,
  },
  detailCard: {
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
    overflow: "hidden",
  },
});
