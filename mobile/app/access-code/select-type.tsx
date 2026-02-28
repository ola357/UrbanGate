import React, { useState } from 'react';
import { View, Text, ScrollView } from 'react-native';
import { useRouter } from 'expo-router';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { ChevronLeft, User, Users, Infinity, KeyRound } from 'lucide-react-native';
import { Button } from '@/components/ui/Buttons';
import { VisitorTypeCard } from '@/features/access-code/components/VisitorTypeCard';

type VisitorType = 'single' | 'group' | 'multiple' | 'all-gate';

const VISITOR_TYPES = [
  {
    key: 'single' as VisitorType,
    label: 'Single visitor',
    desc: 'One-time pass for a single guest. Expires after entry or on your set time.',
    Icon: User,
  },
  {
    key: 'group' as VisitorType,
    label: 'Group visit',
    desc: 'One pass for multiple guests arriving together. Ideal for small gatherings or family visits.',
    Icon: Users,
  },
  {
    key: 'multiple' as VisitorType,
    label: 'Multi-pass',
    desc: 'Use for same visitor to enter multiple times. Best for short stays, staff, or maintenance work.',
    Icon: Infinity,
  },
  {
    key: 'all-gate' as VisitorType,
    label: 'All-gate access',
    desc: 'Generates a separate pass for each entrance. Use when visitors may enter through different gates.',
    Icon: KeyRound,
  },
];

export default function SelectTypeScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const [selectedType, setSelectedType] = useState<VisitorType>('single');

  return (
    <View style={{ flex: 1, backgroundColor: '#00483C' }}>
      {/* Header */}
      <View
        style={{
          paddingTop: insets.top + 16,
          paddingHorizontal: 16,
          paddingBottom: 16,
          flexDirection: 'row',
          alignItems: 'center',
        }}
      >
        <ChevronLeft
          size={24}
          color="white"
          onPress={() => router.back()}
          style={{ cursor: 'pointer' } as any}
        />
        <Text
          style={{
            flex: 1,
            textAlign: 'center',
            color: 'white',
            fontWeight: '700',
            fontSize: 17,
          }}
        >
          New visitor code
        </Text>
        <View style={{ width: 24 }} />
      </View>

      {/* Body */}
      <View style={{ flex: 1, backgroundColor: '#F5F5F5', borderTopLeftRadius: 20, borderTopRightRadius: 20 }}>
        <ScrollView
          contentContainerStyle={{ paddingHorizontal: 16, paddingTop: 24, paddingBottom: 24, gap: 12 }}
          showsVerticalScrollIndicator={false}
        >
          {VISITOR_TYPES.map((item) => (
            <VisitorTypeCard
              key={item.key}
              selected={selectedType === item.key}
              Icon={item.Icon}
              label={item.label}
              desc={item.desc}
              onPress={() => setSelectedType(item.key)}
            />
          ))}
        </ScrollView>

        {/* Footer */}
        <View style={{ paddingBottom: insets.bottom + 16, paddingHorizontal: 16, backgroundColor: '#F5F5F5' }}>
          <Button
            fullWidth
            onPress={() =>
              router.push({
                pathname: '/access-code/visitor-details',
                params: { type: selectedType },
              })
            }
          >
            Continue
          </Button>
        </View>
      </View>
    </View>
  );
}
