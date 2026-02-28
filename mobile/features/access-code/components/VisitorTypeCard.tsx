import React from 'react';
import { TouchableOpacity, View, Text } from 'react-native';
import { LucideIcon } from 'lucide-react-native';
import Colors from '@/constants/Colors';
import { useColorScheme } from '@/components/useColorScheme';

interface VisitorTypeCardProps {
  selected: boolean;
  Icon: LucideIcon;
  label: string;
  desc: string;
  onPress: () => void;
}

export function VisitorTypeCard({ selected, Icon, label, desc, onPress }: VisitorTypeCardProps) {
  const colorScheme = useColorScheme() ?? 'light';
  const colors = Colors[colorScheme];

  return (
    <TouchableOpacity
      onPress={onPress}
      style={{
        backgroundColor: colors.card,
        borderRadius: 12,
        borderWidth: 1,
        borderColor: selected ? colors.tint : colors.border,
        paddingHorizontal: 16,
        paddingTop: 14,
        paddingBottom: selected ? 0 : 14,
      }}
    >
      <View style={{ flexDirection: 'row', alignItems: 'center' }}>
        <Icon size={20} color={colors.textTertiary} />
        <View style={{ flex: 1, marginHorizontal: 12 }}>
          <Text style={{ fontWeight: '700', fontSize: 15, color: colors.text }}>{label}</Text>
          <Text style={{ fontSize: 13, color: colors.textTertiary, marginTop: 2 }}>{desc}</Text>
        </View>
        <View
          style={{
            width: 24,
            height: 24,
            borderRadius: 12,
            borderWidth: 2,
            borderColor: selected ? colors.tint : colors.border,
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          {selected && (
            <View
              style={{
                width: 14,
                height: 14,
                borderRadius: 7,
                backgroundColor: colors.tint,
              }}
            />
          )}
        </View>
      </View>
      {selected && (
        <View
          style={{
            height: 2,
            backgroundColor: colors.tint,
            marginHorizontal: -16,
            marginTop: 12,
          }}
        />
      )}
    </TouchableOpacity>
  );
}
